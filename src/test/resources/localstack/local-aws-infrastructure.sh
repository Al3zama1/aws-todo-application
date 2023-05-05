#!/bin/sh

awslocal sqs create-queue --queue-name application-todo-sharing

awslocal ses verify-email-identity --email-address noreply@showcasecloudproject.com
awslocal ses verify-email-identity --email-address info@showcasecloudproject.com
awslocal ses verify-email-identity --email-address abran@showcasecloudproject.com
awslocal ses verify-email-identity --email-address mario@showcasecloudproject.com
awslocal ses verify-email-identity --email-address claudia@showcasecloudproject.com

awslocal dynamodb create-table \
    --table-name local-todo-app-breadcrumb \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=10 \

echo "Initialized."
