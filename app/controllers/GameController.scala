package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import components._
import _root_.data._
import models.admin.games._
import models.FieldExtensions._
import models.FormExtensions._
import extensions.DateTimeExtensions._
import security.Roles

trait SeasonIsRequired extends Secured with SeasonComponentImpl {
  def HasSeason(seasonId: Int)(f: => Season => Request[AnyContent] => Result) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    seasonService.getSeasonById(seasonId)
      .map(f(_)(request))
      .getOrElse(Results.NotFound(s"Season $seasonId not found"))
  }
}

object GameController extends Controller 
  with SeasonIsRequired 
  with ProvidesHeader 
  with TeamComponentImpl 
  with GameComponentImpl
  with PlayerComponentImpl {
  
  def index(implicit seasonId: Int) = HasSeason(seasonId) { username => implicit request => 
    val data = gameService.getGamesBySeasonId(seasonId) map(game => (game, teamService.getTeamsForGame(game.gameId)))

    Ok(views.html.admin.games.index(GamesModel.toModel(seasonId, data, routes.GameController)))
  }

  def create(implicit seasonId: Int) = HasSeason(seasonId) { username => implicit request => 
    Ok(views.html.admin.games.edit(EditGameModel.toModel(seasonId, 0, 0, GameTypes.Regular.id, 0, 0), EditGameErrors.empty))
  }

  def saveNew(implicit seasonId: Int) = HasSeason(seasonId) { username => implicit request => 
    updateGame(model => gameService.addGame(model.toEntity(seasonId)))
  }

  def edit(seasonId: Int, gameId: Int) = HasGame(seasonId, gameId) { game => implicit request =>
    implicit val iSeasonId = seasonId

    Ok(views.html.admin.games.edit(EditGameModel.toModel(seasonId, game), EditGameErrors.empty))
  }

  def saveExisting(seasonId: Int, gameId: Int) = HasGame(seasonId, gameId) { game => implicit request =>
    implicit val iSeasonId = seasonId
   
    updateGame(model => gameService.updateGame(model.toEntity(seasonId)))
  }

  def remove(seasonId: Int, gameId: Int) = HasGame(seasonId, gameId) { username => implicit request => 
    implicit val iSeasonId = seasonId

    if(gameService.removeGame(gameId)) Redirect(routes.GameController.index(seasonId))
    else InternalServerError(s"Could not remove game: $gameId")
  }

  def stats(seasonId: Int, gameId: Int) = HasSeason(seasonId) { username => implicit request => 
    implicit val iSeasonId = seasonId

    gameService.getGame(gameId).map { game =>
      Ok(views.html.admin.games.stats(StatsModel.toModel(seasonId, game, gameService.getDemoStatusForGame(gameId), gameService.getRoundStats(gameId))))
    } getOrElse(NotFound(s"Game with Id: $gameId not found."))
  }

  def uploadStats(seasonId: Int, gameId: Int) = HasSeason(seasonId) { username => implicit request => 
    gameService.getGame(gameId) map { game => 
      request.body.asMultipartFormData map { data => 
        import scala.io.{ Source, Codec }
        import java.io.File
        
        data.file("log") map { log => 
          import StatsModel._

          val source = Source.fromFile(log.ref.file)(Codec.ISO8859)
          val stats = gameService.parseGameResults(gameId, source)
          val playerNames = stats.flatMap(_._2).map(_._1).toSet

          playerService.batchCreatePlayerFromName(playerNames)
          gameService.addRoundResults(gameId, stats)

          val updatedGame = gameService.getGame(gameId).get

          Ok(Json.toJson(StatsModel.toModel(seasonId, updatedGame, gameService.getDemoStatusForGame(gameId), gameService.getRoundStats(gameId))))
        } getOrElse(BadRequest("File `log` was not found."))
      } getOrElse(BadRequest("Invalid form submission."))
    } getOrElse(NotFound(s"Game with Id: $gameId not found."))
  }

  def uploadDemo(seasonId: Int, gameId: Int, playerId: Int) = HasSeason(seasonId) { username => implicit request => 
    gameService.getGame(gameId) map { game => 
      playerService.getPlayer(playerId) map { player => 
        request.body.asMultipartFormData map { data => 
          data.file("demo") map { demo => 
            gameService.addDemo(gameId, playerId, demo.filename, demo.ref.file) map { result => 
              import StatsModel._

              Ok(Json.toJson(GameDemoModel.toModel(player.playerName, result)))
            } getOrElse(InternalServerError("Unable to upload demo"))
          } getOrElse(BadRequest("File `demo` was not found."))
        } getOrElse(BadRequest("Invalid form submission."))
      } getOrElse(NotFound(s"Player with Id $playerId was not found."))
    } getOrElse(NotFound(s"Game with Id: $gameId not found."))
  }

  def removeRound(seasonId: Int, gameId: Int, roundId: Int) = HasSeason(seasonId) { username => implicit request => 
    gameService.getGame(gameId) map { game => 
      gameService.getRound(roundId) map { round => 
        gameService.disableRound(round) map { disabledRound => 
          Ok(Json.toJson(roundId))
        } getOrElse(InternalServerError(s"Unable to disable round: $roundId"))
      } getOrElse(NotFound(s"Round with Id: $roundId not found."))
    } getOrElse(NotFound(s"Game with Id: $gameId not found."))
  }

  def updateRound(seasonId: Int, gameId: Int, roundId: Int) = HasSeason(seasonId) { username => implicit request => 
    gameService.getGame(gameId) map { game => 
      gameService.getRound(roundId) map { round => 
        import StatsModel._
        
        handleJsonPost[RoundStatsModel] { newData => 
          gameService.updateRoundResult(RoundStatsModel.toEntity(roundId, newData)) map { result => 
            gameService.getRoundStatsForPlayer(gameId, roundId, newData.playerId) map { stats => 
              Ok(Json.toJson(RoundStatsModel.toModel(stats)))
            } getOrElse(InternalServerError(s"Could not get round stats for gameId: $gameId, playerId: ${newData.playerId}"))
          } getOrElse(InternalServerError(s"Cound not update round result with Id: $roundId"))
        } 
      } getOrElse(NotFound(s"Round with Id: $roundId not found."))
    } getOrElse(NotFound(s"Game with Id: $gameId not found."))
  }

  private def HasGame(seasonId: Int, gameId: Int)(f: => Game => Request[AnyContent] => Result) = HasSeason(seasonId) { username => implicit request => 
    gameService.getGame(gameId).map { game => 
      if(game.status == GameStatus.Pending) f(game)(request)
      else BadRequest("Cannot alter an already completed game.")
    } getOrElse(NotFound(s"Game with Id: $gameId not found."))
  }

  private def updateGame(saveAction: EditGamePostModel => Boolean)(implicit request: Request[AnyContent], seasonId: Int) : Result = 
    EditGameForm().bindFromRequest.fold(
      content => {
        val gameIdError = content("gameId").formattedMessage
        val weekIdError = content("selectedWeekId").formattedMessage
        val gameTypeIdError = content("selectedGameTypeId").formattedMessage
        val team1IdError = content("selectedTeam1Id").formattedMessage
        val team2IdError = content("selectedTeam2Id").formattedMessage

        val editGameModel = EditGameModel.toModel(seasonId, gameIdError._1.toInt, weekIdError._1.toInt, gameTypeIdError._1.toInt, team1IdError._1.toInt, team2IdError._1.toInt)
        val errorsModel = EditGameErrors(gameIdError._2, weekIdError._2, gameTypeIdError._2, team1IdError._2, team2IdError._2, content.formattedMessages)

        BadRequest(views.html.admin.games.edit(editGameModel, errorsModel))
      },
      model => 
        if(saveAction(model)) Redirect(routes.GameController.index(seasonId))
        else InternalServerError("Unable to save game")
    )
}
