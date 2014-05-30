package controllers

import play.api._
import play.api.mvc._
import components._
import _root_.data.News
import models.admin.news._
import models.FieldExtensions._
import models.FormExtensions._
import security.Roles
import org.joda.time.{ DateTime, DateTimeZone }

object NewsController extends Controller with ProvidesHeader with Secured with NewsComponentImpl with ProfileComponentImpl {
  val couldNotUpdateNewsItem = InternalServerError("Could not create news item")

  def index(page: Int = 1, size: Int = 15) = IsAuthenticated(Roles.Admin) { username => implicit request =>
    Ok(views.html.admin.news.index(AdminNews.toModels(newsService.getAllNews, username, routes.NewsController)))
  }

  def create = IsAuthenticated { username => implicit request =>
    Ok(views.html.admin.news.edit(EditNews.empty, EditNewsErrors()))
  }

  def saveNew = IsAuthenticated { username => implicit request =>
    val now = new DateTime(DateTimeZone.UTC)
    
    updateNews((model, profileId) => newsService.insertNews(News(model.newsId, model.subject, now, now, model.content, profileId)), username)
  }

  def saveExisting(id: Int) = IsAuthenticated { username => implicit request =>
    updateNews((news, profileId) => newsService.updateNews(news.newsId, news.subject, news.content), username)
  }

  def edit(id: Int) = IsAuthenticated { username => implicit request =>
    newsService.getNewsById(id) match {
      case Some(x) => Ok(views.html.admin.news.edit(EditNews.toModel(x), EditNewsErrors()))
      case None => Redirect(routes.NewsController.create)
    }
  }

  def remove(id: Int) = IsAuthenticated { username => implicit request =>
    newsService.removeNewsItem(id) match {
      case true => Redirect(routes.NewsController.index)
      case false => InternalServerError("Could not remove news item")
    }
  }

  def updateNews(saveAction: (EditNews, Int) => Boolean, username: String)(implicit request: Request[AnyContent]) : Result = 
    EditNewsForm().bindFromRequest.fold(
      content => {
        val subjectError = content("subject").formattedMessage
        val contentError = content("content").formattedMessage
        val editNewsModel = EditNews(0, subjectError._1, contentError._1)
        val errorsModel = EditNewsErrors(subjectError._2, contentError._2) 

        BadRequest(views.html.admin.news.edit(editNewsModel, errorsModel))
      },
      news => {
        profileService.getProfileIdByUsername(username) match {
          case Some(id) => {
            saveAction(news, id) match {
              case true => Redirect(routes.NewsController.index)
              case false => couldNotUpdateNewsItem
            }
          }
          case None => couldNotUpdateNewsItem
        }
      }
    )
}
