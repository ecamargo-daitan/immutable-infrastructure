#!/usr/bin/env bash

set -ex

cd ${WORKSPACE}/ansible

virtualenv --system-site-packages venv
source venv/bin/activate
pip install -r requirements.txt

export ANSIBLE_HOST_KEY_CHECKING=False
ansible-playbook -i inventory/ec2.py -e environment_name=${ENVIRONMENT_NAME} playbooks/api.yml
