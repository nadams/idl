package components

import data._
import services._

trait TeamRegistry
extends TeamServiceComponent
with TeamRepositoryComponent

trait TeamComponentImpl
extends TeamRegistry
with TeamServiceComponentImpl
with TeamRepositoryComponentImpl