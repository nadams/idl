package components

import data._
import services._

trait NewsRegistry
extends NewsServiceComponent
with NewsRepositoryComponent

trait NewsComponentImpl
extends NewsRegistry
with NewsServiceComponentImpl
with NewsRepositoryComponentImpl
