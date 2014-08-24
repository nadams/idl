package controllers

import play.api._
import play.api.mvc._
import org.joda.time.{ DateTime, DateTimeZone }
import components._
import _root_.data.News
import models.admin.news._
import models.FieldExtensions._
import models.FormExtensions._
import security.Roles

object NewsController extends Controller with ProvidesHeader with Secured with NewsComponentImpl with ProfileComponentImpl {
  val couldNotUpdateNewsItem = InternalServerError("Could not create news item")

  def index(page: Int = 1, size: Int = 15) = IsAuthenticated(Roles.Admin) { username => implicit request =>
    Ok(views.html.admin.news.index(AdminNews.toModels(newsService.getAllNews, username, routes.NewsController)))
  }

  def create = IsAuthenticated(Roles.Admin) { username => implicit request =>
    Ok(views.html.admin.news.edit(EditNews.empty, EditNewsErrors()))
  }

  def saveNew = IsAuthenticated(Roles.Admin) { username => implicit request =>
    val now = new DateTime(DateTimeZone.UTC)
    
    updateNews((model, profileId) => newsService.insertNews(News(model.newsId, model.subject, now, now, model.content, profileId)), username)
  }

  def saveExisting(id: Int) = IsAuthenticated(Roles.Admin) { username => implicit request =>
    updateNews((news, profileId) => newsService.updateNews(news.newsId, news.subject, news.content), username)
  }

  def edit(id: Int) = IsAuthenticated(Roles.Admin) { username => implicit request =>
    newsService.getNewsById(id)
      .map(newsItem => Ok(views.html.admin.news.edit(EditNews.toModel(newsItem), EditNewsErrors())))
      .getOrElse(Redirect(routes.NewsController.create))
  }

  def remove(id: Int) = IsAuthenticated(Roles.Admin) { username => implicit request =>
    if(newsService.removeNewsItem(id)) {
      Redirect(routes.NewsController.index)
    } else {
      InternalServerError("Could not remove news item")
    }
  }

  private def updateNews(saveAction: (EditNews, Int) => Boolean, username: String)(implicit request: Request[AnyContent]) : Result = 
    EditNewsForm().bindFromRequest.fold(
      content => {
        val subjectError = content("subject").formattedMessage
        val contentError = content("content").formattedMessage
        val editNewsModel = EditNews(0, subjectError._1, contentError._1)
        val errorsModel = EditNewsErrors(subjectError._2, contentError._2) 

        BadRequest(views.html.admin.news.edit(editNewsModel, errorsModel))
      },
      news => {
        profileService.getProfileIdByUsername(username).map { id =>
          if(saveAction(news, id)) {
            Redirect(routes.NewsController.index)
          } else {
            couldNotUpdateNewsItem
          }
        }.getOrElse(couldNotUpdateNewsItem)
      }
    )
}
