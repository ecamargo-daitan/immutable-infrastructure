#!/usr/bin/env bash

set -ex

cd ${WORKSPACE}/deploy

virtualenv --system-site-packages venv
source venv/bin/activate
pip install -r requirements.txt

python deploy.py ${BUILD_ID} ami-db48ada1 ${ENVIRONMENT_NAME}
