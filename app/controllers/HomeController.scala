package controllers

import play.api._
import play.api.mvc._
import models._
import components._

object HomeController extends Controller with ProvidesHeader with NewsComponentImpl {
  def index = Action { implicit request =>
    val model = NewsModel(newsService.getPagedNews())

    Ok(views.html.index(model))
  }
}