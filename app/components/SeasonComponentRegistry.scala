package components

import data._
import services._

trait SeasonRegistry
extends SeasonServiceComponent
with SeasonRepositoryComponent

trait SeasonComponentImpl
extends SeasonRegistry
with SeasonServiceComponentImpl
with SeasonRepositoryComponentImpl
