from argparse import ArgumentParser
import subprocess
import boto3
import urllib2
import os
import time


def parse_args():
    parser = ArgumentParser(description='Deploy the application.')
    parser.add_argument('version', type=str, help='The version number for this deployment.')
    parser.add_argument('ami_id', type=str, help='Api AMI id to deploy.')
    parser.add_argument('env_name', type=str, help='Environment name.')
    return parser.parse_args()


def deploy(version, ami_id, env_name):
    print("Deploying version {} using {}.".format(version, ami_id))

    command = 'ansible-playbook '
    command += '-e "api_ami_id={}" '.format(ami_id)
    command += '-e "version={}" '.format(version)
    command += '-e "environment_name={}" '.format(env_name)
    command += 'playbooks/deploy.yml'

    subprocess.call(command, shell=True)

def main():
    args = parse_args()
    version = args.version
    ami_id = args.ami_id
    env_name = args.env_name

    deploy(version, ami_id, env_name)

    print('Version {} deployed.'.format(version))

if __name__ == '__main__':
    main()
