package controllers

import play.api._
import play.api.mvc._
import components._
import _root_.data.News
import models.news._
import models.FieldExtensions._
import models.FormExtensions._
import org.joda.time.{ DateTime, DateTimeZone }

object NewsController extends Controller with ProvidesHeader with Secured with NewsComponentImpl  with ProfileComponentImpl {
  def index = IsAuthenticated { username => implicit request =>
    Ok(views.html.news.index(AdminNews.toModels(newsService.getAllNews, username, routes.NewsController)))
  }

  def create = IsAuthenticated { username => implicit request =>
    Ok(views.html.news.edit(EditNews(), EditNewsErrors()))
  }

  def saveNew = IsAuthenticated { username => implicit request =>
    EditNewsForm().bindFromRequest.fold(
      content => {
        val subjectError = content("subject").formattedMessage
        val contentError = content("content").formattedMessage
        val editNewsModel = EditNews(0, subjectError._1, contentError._1)
        val errorsModel = EditNewsErrors(subjectError._2, contentError._2) 

        BadRequest(views.html.news.edit(editNewsModel, errorsModel))
      },
      news => {
        profileService.getProfileIdByUsername(username) match {
          case Some(id) => {
            val now = new DateTime(DateTimeZone.UTC)
            val newsEntity = News(news.newsId, news.subject, now, now, news.content, id)
            newsService.insertNews(newsEntity) 

            Redirect(routes.NewsController.index)
          }
          case None => InternalServerError("Could not create news item")
        }
      }
    )
  }

  def saveExisting(id: Int) = IsAuthenticated { username => implicit request =>
    Ok("")
  }

  def edit(id: Int) = IsAuthenticated { username => implicit request =>
    newsService.getNewsById(id) match {
      case Some(x) => Ok(views.html.news.edit(EditNews(x), EditNewsErrors()))
      case None => Redirect(routes.NewsController.create)
    }
  }

  def remove(id: Int) = IsAuthenticated { username => implicit request =>
    newsService.removeNewsItem(id) match {
      case true => Redirect(routes.NewsController.index)
      case false => InternalServerError("Could not remove news item")
    }
  }
}
