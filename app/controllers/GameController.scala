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

    Ok(views.html.admin.games.index(GamesModel.toModel(data)))
  }

  def create(implicit seasonId: Int) = HasSeason(seasonId) { username => implicit request => 
    Ok(views.html.admin.games.edit(EditGameModel.toModel(seasonId, 0, 0, 0, 0), EditGameErrors.empty))
  }

  def saveNew(implicit seasonId: Int) = HasSeason(seasonId) { username => implicit request => 
    EditGameForm().bindFromRequest.fold (
      formWithErrors => { 
        play.Logger.info(formWithErrors.toString)
        BadRequest("placeholder") 
      },
      model => {
        if(gameService.addGame(model.toEntity(seasonId))) Redirect(routes.GameController.index(seasonId))
        else InternalServerError("Unable to save game")
      }
    )
  }

  def edit(seasonId: Int, gameId: Int) = HasSeason(seasonId) { username => implicit request =>
    Ok("")
  }

  def saveExisting(seasonId: Int, gameId: Int) = HasSeason(seasonId) { username => implicit request => 
    Ok("")
  }
}
