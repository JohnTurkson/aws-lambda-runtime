package com.johnturkson.aws.runtime.example.functions

import kotlinx.serialization.Serializable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticImmutableTableSchema
import kotlin.properties.Delegates

@Serializable
data class User(val metadata: UserMetadata)

@Serializable
data class UserMetadata(val id: String)

class UserBuilder {
    private var metadata by Delegates.notNull<UserMetadata>()
    
    fun metadata(metadata: UserMetadata): UserBuilder {
        this.metadata = metadata
        return this
    }
    
    fun build(): User {
        return User(metadata)
    }
}

class UserMetadataBuilder {
    private var id by Delegates.notNull<String>()
    
    fun id(id: String): UserMetadataBuilder {
        this.id = id
        return this
    }
    
    fun build(): UserMetadata {
        return UserMetadata(id)
    }
}

object UserDefinition {
    val SCHEMA: StaticImmutableTableSchema<User, UserBuilder> =
        TableSchema.builder(User::class.java, UserBuilder::class.java)
            .newItemBuilder(::UserBuilder, UserBuilder::build)
            .flatten(UserMetadataDefinition.SCHEMA, User::metadata, UserBuilder::metadata)
            .build()
    
    val DynamoDbEnhancedAsyncClient.UserTable: DynamoDbAsyncTable<User>
        get() = this.table(System.getenv("USER_TABLE"), SCHEMA)
    
    val DynamoDbEnhancedClient.UserTable: DynamoDbTable<User>
        get() = this.table(System.getenv("USER_TABLE"), SCHEMA)
}

object UserMetadataDefinition {
    val SCHEMA: StaticImmutableTableSchema<UserMetadata, UserMetadataBuilder> =
        TableSchema.builder(UserMetadata::class.java, UserMetadataBuilder::class.java)
            .newItemBuilder(::UserMetadataBuilder, UserMetadataBuilder::build)
            .addAttribute(String::class.java) { attribute ->
                attribute.name("id")
                    .getter(UserMetadata::id)
                    .setter(UserMetadataBuilder::id)
                    .tags(
                        StaticAttributeTags.primaryPartitionKey()
                    )
            }
            .build()
}
