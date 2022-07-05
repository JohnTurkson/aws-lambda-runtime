package com.johnturkson.aws.runtime.infrastructure

import software.amazon.awscdk.App
import software.amazon.awscdk.Duration
import software.amazon.awscdk.RemovalPolicy
import software.amazon.awscdk.Stack
import software.amazon.awscdk.services.dynamodb.Attribute
import software.amazon.awscdk.services.dynamodb.AttributeType
import software.amazon.awscdk.services.dynamodb.BillingMode
import software.amazon.awscdk.services.dynamodb.Table
import software.amazon.awscdk.services.lambda.Architecture
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.lambda.Runtime

fun main() {
    val app = App()
    
    val stack = Stack(app, "ExampleStack")
    
    val table = Table.Builder.create(stack, "ExampleTable")
        .billingMode(BillingMode.PAY_PER_REQUEST)
        .removalPolicy(RemovalPolicy.DESTROY)
        .partitionKey(Attribute.builder().name("id").type(AttributeType.STRING).build())
        .build()
    
    val function = Function.Builder.create(stack, "ExampleFunction")
        .handler("com.johnturkson.aws.runtime.example.ExampleFunction")
        .code(Code.fromAsset("../example/build/lambda/image/bootstrap.zip"))
        .environment(mapOf("USER_TABLE" to table.tableName))
        .timeout(Duration.seconds(1))
        .memorySize(1024)
        .runtime(Runtime.PROVIDED_AL2)
        .architecture(Architecture.X86_64)
        .build()
    
    val function2 = Function.Builder.create(stack, "ExampleFunction2")
        .handler("com.johnturkson.aws.runtime.example.ExampleFunction2")
        .code(Code.fromAsset("../example/build/lambda/image/bootstrap.zip"))
        .timeout(Duration.seconds(1))
        .memorySize(1024)
        .runtime(Runtime.PROVIDED_AL2)
        .architecture(Architecture.X86_64)
        .build()
    
    table.grantReadWriteData(function)
    
    app.synth()
}
