package components

import data._
import services._

trait GameRegistry
extends GameServiceComponent
with GameRepositoryComponent

trait GameComponentImpl
extends GameRegistry
with GameServiceComponentImpl
with GameRepositoryComponentImpl