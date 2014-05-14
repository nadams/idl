package controllers

import play.api._
import play.api.mvc._
import models.news._
import components._

object NewsController extends Controller with ProvidesHeader with Secured with NewsComponentImpl {
  def index = IsAuthenticated { username => implicit request =>
    Ok(views.html.news.index(AdminNews(Seq.empty[AdminNewsListItem])))
  }

  def create = IsAuthenticated { username => implicit request =>
    Ok(views.html.news.edit(EditNews()))
  }

  def saveNew = IsAuthenticated { username => implicit request =>
    Ok("")
  }

  def saveExisting(id: Int) = IsAuthenticated { username => implicit request =>
    Ok("")
  }

  def edit(id: Int) = IsAuthenticated { username => implicit request =>
    Ok(views.html.news.edit(EditNews()))
  }

  def remove(id: Int) = IsAuthenticated { username => implicit request =>
    newsService.removeNewsItem(id) match {
      case true => Redirect(routes.NewsController.index)
      //case false => Ok(views.html.news.edit(newsService.getNewsById(id)))
      case false => InternalServerError("Could not remove news item")
    }
  }
}