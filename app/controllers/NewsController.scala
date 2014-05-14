package controllers

import play.api._
import play.api.mvc._
import models.news._

object NewsController extends Controller with ProvidesHeader with Secured {
  def index = IsAuthenticated { username => implicit request =>
    Ok(views.html.news.index(AdminNews(Seq.empty[AdminNewsListItem])))
  }

  def create = IsAuthenticated { username => implicit request =>
    Ok("")
  }

  def saveNew = IsAuthenticated { username => implicit request =>
    Ok("")
  }

  def saveExisting(id: Int) = IsAuthenticated { username => implicit request =>
    Ok("")
  }

  def edit(id: Int) = IsAuthenticated { username => implicit request =>
    Ok("")
  }

  def remove(id: Int) = IsAuthenticated { username => implicit request =>
    Ok("")
  }
}