package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import org.joda.time.{ DateTime, DateTimeZone }
import components._
import _root_.data._
import models.admin.games._
import models.FieldExtensions._
import models.FormExtensions._
import extensions.DateTimeExtensions._
import security.Roles

object GameController extends Controller with Secured with ProvidesHeader with TeamComponentImpl with SeasonComponentImpl with GameComponentImpl {
  def index(seasonId: Int) = HasSeason(seasonId) { username => implicit request => 
    val data = gameService.getGamesBySeasonId(seasonId) map(game => (game, teamService.getTeamsForGame(game.gameId)))

    Ok(views.html.admin.games.index(GamesModel.toModel(data)))
  }

  private def HasSeason(seasonId: Int)(f: => Season => Request[AnyContent] => Result) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    seasonService.getSeasonById(seasonId)
      .map(f(_)(request))
      .getOrElse(NotFound(s"Season $seasonId not found"))
  }
}
