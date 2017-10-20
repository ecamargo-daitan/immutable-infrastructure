#!/usr/bin/env bash

set -ex

cd ${WORKSPACE}/deploy

env=${ENVIRONMENT_NAME}

virtualenv --system-site-packages venv
source venv/bin/activate
pip install -r requirements.txt

python deploy.py ${BUILD_ID} ${env}
