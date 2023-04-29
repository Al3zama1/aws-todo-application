#!/bin/sh

awslocal sqs create-queue --queue-name todo-sharing

awslocal ses verify-email-identity --email-address noreply@abranlezama.dev
awslocal ses verify-email-identity --email-address info@abranlezama.dev
awslocal ses verify-email-identity --email-address abran@abranlezama.dev
awslocal ses verify-email-identity --email-address mario@abranlezama.dev
awslocal ses verify-email-identity --email-address claudia@abranlezama.dev

awslocal dynamodb create-table \
    --table-name local-todo-app-breadcrumb \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=10 \

echo "Initialized."
