from troposphere import Output, Ref, Template, Parameter, Join
from troposphere.sns import Topic, SubscriptionResource


template = Template()

environment = template.add_parameter(Parameter(
    'Environment',
    Type='String',
    Default='test'
))

email = template.add_parameter(Parameter(
    'Email',
    Type='String'
))

topic = template.add_resource(Topic(
    'SnsTopic',
    DisplayName='sns-topic',
    TopicName=Join('-', [Ref(environment), 'sns-topic']),
))

template.add_resource(SubscriptionResource(
    'Subscription',
    Protocol='email',
    Endpoint=Ref(email),
    TopicArn=Ref(topic)
))

template.add_output(Output(
    'TopicArn',
    Description='SNS Topic ARN',
    Value=Ref(topic)
))

print(template.to_json())
