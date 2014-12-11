package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import components.{ ProfileComponentImpl, PlayerComponentImpl, TeamComponentImpl }
import models.profile._
import _root_.data._
import models.FieldExtensions._
import models.FormExtensions._

object ProfileController
  extends Controller
  with Secured
  with ProvidesHeader
  with ProfileComponentImpl
  with TeamComponentImpl
  with PlayerComponentImpl {

  def login = IdlAction { implicit request =>
    Ok(views.html.profile.login(LoginModel(), Seq.empty[String]))
  }

  def performLogin = IdlAction { implicit request =>
    LoginForm().bindFromRequest.fold(
      errors => {
        val loginModel = LoginModel(errors("username").formattedMessage._1, "")
        val errorMessages = errors.formattedMessages

        BadRequest(views.html.profile.login(loginModel, errorMessages))
      },
      user => request.queryString.get(Secured.returnUrl)
        .map(url => Redirect(url.head))
        .getOrElse(Redirect(routes.HomeController.index))
        .withSession(SessionKeys.username -> user.username)
    )
  }

  def index = IsAuthenticated { username => implicit request =>
    profileService.getByUsername(username).fold(profileNotFound(username)){ profile =>
      Ok(
        views.html.profile.index(
          IndexModel.toModel(
            profile.profileId,
            playerService.profileIsPlayer(profile.profileId),
            profile,
            playerService.getPlayerProfileRecordsForProfile(profile.profileId),
            playerService.getFellowPlayersForProfile(profile.profileId)
          )
        )
      )
    }
  }

  def updatePassword = IsAuthenticated { username => implicit request =>
    ChangePasswordForm().bind(request.body.asJson.get).fold(
      errors => {
        val currentPassword = errors("currentPassword").formattedMessage._2.getOrElse("")
        val newPassword = errors("newPassword").formattedMessage._2.getOrElse("")
        val confirmPassword = errors("confirmPassword").formattedMessage._2.getOrElse("")

        BadRequest(Json.toJson(PasswordModelErrors(currentPassword, newPassword, confirmPassword, errors.formattedMessages)))
      },
      result =>
        if(profileService.updateProfilePassword(username, result.newPassword)) {
          Ok(Json.toJson("Success"))
        } else {
          InternalServerError("Could not update password")
        }
    )
  }

  def logout = IdlAction { implicit request =>
    Redirect(routes.HomeController.index).withSession(request.session - SessionKeys.username)
  }

  def becomePlayer = IsAuthenticated { username => implicit request =>
    profileService.getByUsername(username) map { profile =>
      playerService.makeProfileAPlayer(profile) map { record =>
        Ok(Json.toJson(BecomePlayerResultModel.toModel(record, "Congratulations, you are now an IDL player!")))
      } getOrElse(InternalServerError(Json.toJson("Cannot add you as an IDL player.")))
    } getOrElse(profileNotFound(username))
  }

  def games = IsAuthenticated { username => implicit request =>
    Ok("")
  }

  def addPlayer(playerName: String) = IsAuthenticated { username => implicit request =>
    profileService.getByUsername(username) map { profile =>
      playerService.createOrAddPlayerToProfile(profile.profileId, playerName) map { tryResult =>
        tryResult.map { player =>
          playerService.getPlayerProfile(profile.profileId, player.playerId) map { playerProfile =>
            playerService.getPlayerProfileRecord(playerProfile.profileId, playerProfile.playerId) map { playerProfileRecord =>
              Ok(Json.toJson(AddPlayerNameResultModel.toModel(playerProfileRecord)))
            } getOrElse(InternalServerError(s"Could not get playerProfilRecord"))
          } getOrElse(InternalServerError(s"Could not get player profile"))
        } getOrElse(InternalServerError("Player name already exists"))
      } getOrElse(BadRequest("You have too many player names"))
    } getOrElse(profileNotFound(username))
  }

  def removePlayer(playerId: Int) = IsAuthenticated { username => implicit request =>
    profileService.getByUsername(username) map { profile =>
      if(playerService.removePlayerFromProfile(profile.profileId, playerId)) Ok(Json.toJson(playerId))
      else BadRequest(s"Could not remove player $playerId from $username")
    } getOrElse(profileNotFound(username))
  }

  def updateDisplayName(displayName: String) = IsAuthenticated { username => implicit request =>
    profileService.getByUsername(username) map { profile =>
      profileService.updateDisplayName(profile, displayName) map { updatedProfile =>
        Ok(Json.toJson(updatedProfile.displayName))
      } getOrElse(InternalServerError("Could not update displayName"))
    } getOrElse(profileNotFound(username))
  }

  def requestToJoinTeam = IsAuthenticated { username => implicit request =>
    import models.profile.IndexModel._

    handleJsonPost[RequestToJoinTeamModel] { model =>
      profileService.getByUsername(username) map { profile =>
        teamService.getTeamByName(model.teamName) map { team =>
          teamService.assignPlayersToTeam(team.teamId, Seq(model.playerId)).headOption map { teamId =>
            Ok(Json.toJson(TeamMembershipModel.toModel(playerService.getFellowPlayersForTeamPlayer(profile.profileId, model.playerId, teamId))))
          } getOrElse(InternalServerError("Could not add player to team"))
        } getOrElse(NotFound(s"Team '${model.teamName}' does not exist"))
      } getOrElse(profileNotFound(username))
    }
  }

  def forgotPassword = Action { implicit request =>
    Ok(views.html.profile.forgotPassword(ForgotPasswordModel.empty))
  }

  def recoverAccount = Action { implicit request =>
    RecoverPasswordForm().bindFromRequest.fold(
      formWithErrors => Ok(views.html.profile.forgotPassword(ForgotPasswordModel.empty)),
      form => Redirect(routes.ProfileController.login)
    )
  }

  private def profileNotFound(username: String) = NotFound(s"The profile `$username` was not found.")
}
