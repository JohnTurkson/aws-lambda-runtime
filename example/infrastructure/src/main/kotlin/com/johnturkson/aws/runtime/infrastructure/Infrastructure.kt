package com.johnturkson.aws.runtime.infrastructure

import com.johnturkson.aws.lambda.runtime.generated.functions.ExampleFunction
import software.amazon.awscdk.App
import software.amazon.awscdk.RemovalPolicy
import software.amazon.awscdk.Stack
import software.amazon.awscdk.services.dynamodb.Attribute
import software.amazon.awscdk.services.dynamodb.AttributeType
import software.amazon.awscdk.services.dynamodb.BillingMode
import software.amazon.awscdk.services.dynamodb.Table

fun main() {
    val app = App()
    
    val stack = Stack(app, "ExampleStack")
    
    val table = Table.Builder.create(stack, "ExampleTable")
        .billingMode(BillingMode.PAY_PER_REQUEST)
        .removalPolicy(RemovalPolicy.DESTROY)
        .partitionKey(Attribute.builder().name("id").type(AttributeType.STRING).build())
        .build()
    
    val function = ExampleFunction.Builder.create(stack)
        .environment(mapOf("USER_TABLE" to table.tableName))
        .build()
    
    table.grantReadWriteData(function)
    
    app.synth()
}
