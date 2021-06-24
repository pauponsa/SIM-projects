#!/bin/bash

export PYTHONPATH='.'

scripts/AgentAllotjamentDeploy.sh &
scripts/AgentGestorPaquetsDeploy.sh &
scripts/AgentTransportDeploy.sh &
scripts/AgentExternHotelsDeploy.sh &
scripts/AgentExternVolsDeploy.sh &
scripts/AgentExternActivitatsDeploy.sh &
scripts/AgentExternActivitatsAttractionDeploy.sh &
scripts/AgentExternActivitatsCulturalsDeploy.sh &
scripts/AgentActivitatsDeploy.sh &
scripts/UsuariDeploy.sh




