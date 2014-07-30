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
  with GameComponentImpl {
  
  def index(implicit seasonId: Int) = HasSeason(seasonId) { username => implicit request => 
    val data = gameService.getGamesBySeasonId(seasonId) map(game => (game, teamService.getTeamsForGame(game.gameId)))

    Ok(views.html.admin.games.index(GamesModel.toModel(seasonId, data, routes.GameController)))
  }

  def create(implicit seasonId: Int) = HasSeason(seasonId) { username => implicit request => 
    Ok(views.html.admin.games.edit(EditGameModel.toModel(seasonId, 0, 0, 0, 0), EditGameErrors.empty))
  }

  def saveNew(implicit seasonId: Int) = HasSeason(seasonId) { username => implicit request => 
    updateGame(model => gameService.addGame(model.toEntity(seasonId)))
  }

  def edit(seasonId: Int, gameId: Int) = HasSeason(seasonId) { username => implicit request =>
    gameService.getGame(gameId) map { game => 
      implicit val iSeasonId = seasonId

      Ok(views.html.admin.games.edit(EditGameModel.toModel(seasonId, game), EditGameErrors.empty))
    } getOrElse(NotFound(s"Game with Id: $gameId not found."))
  }

  def saveExisting(seasonId: Int, gameId: Int) = HasSeason(seasonId) { username => implicit request => 
    Ok("")
  }

  def remove(seasonId: Int, gameId: Int) = HasSeason(seasonId) { username => implicit request => 
    Redirect(routes.GameController.index(seasonId))
  }

  private def updateGame(saveAction: EditGamePostModel => Boolean)(implicit request: Request[AnyContent], seasonId: Int) : Result = 
    EditGameForm().bindFromRequest.fold(
      content => {
        val gameIdError = content("gameId").formattedMessage
        val weekIdError = content("selectedWeekId").formattedMessage
        val team1IdError = content("selectedTeam1Id").formattedMessage
        val team2IdError = content("selectedTeam2Id").formattedMessage

        val editGameModel = EditGameModel.toModel(seasonId, gameIdError._1.toInt, weekIdError._1.toInt, team1IdError._1.toInt, team2IdError._1.toInt)
        val errorsModel = EditGameErrors(gameIdError._2, weekIdError._2, team1IdError._2, team2IdError._2, content.formattedMessages)

        BadRequest(views.html.admin.games.edit(editGameModel, errorsModel))
      },
      model => 
        if(saveAction(model)) Redirect(routes.GameController.index(seasonId))
        else InternalServerError("Unable to save game")
    )
}
