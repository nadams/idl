package controllers

import play.api._
import play.api.mvc._
import components._
import models.brackets._

object BracketsController extends Controller with ProvidesHeader with GameComponentImpl {
  def index = IdlAction { implicit request =>
    Ok(views.html.brackets.index(IndexModel.toModel(gameService.getTeamGameRoundResults(None))))
  }
}
