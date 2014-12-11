package controllers

import play.api._
import play.api.mvc._
import models._
import components._

object HomeController extends Controller with ProvidesHeader with NewsComponentImpl {
  def index = IdlAction { implicit request =>
    val model = NewsModel.toModel(newsService.getForumNews())

    Ok(views.html.index(model))
  }
}
