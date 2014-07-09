package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import components._
import _root_.data._
import security.Roles

object GameController extends Controller with ProvidesHeader with Secured with SeasonComponentImpl {
  def index(id: Int) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    Ok(views.html.admin.games.index())
  }

  def HasSeason(seasonId: Int)(f: Season => Result) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    seasonService.getSeasonById(seasonId)
      .map(f(_))
      .getOrElse(NotFound(s"Season $seasonId not found"))
  }
}
