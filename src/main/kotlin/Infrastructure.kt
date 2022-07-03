import software.amazon.awscdk.App
import software.amazon.awscdk.Duration
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.lambda.Architecture
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.lambda.Runtime
import software.constructs.Construct

fun main() {
    val app = App()
    LambdaStack(app, "LambdaStack")
    app.synth()
}

class LambdaStack(
    parent: Construct,
    name: String,
    props: StackProps? = null,
) : Stack(parent, name, props) {
    init {
        Function.Builder.create(this, "LambdaFunction")
            .handler("LambdaFunction")
            .code(Code.fromAsset("build/lambda/image/lambda-sdk.zip"))
            .timeout(Duration.seconds(5))
            .memorySize(1024)
            .runtime(Runtime.PROVIDED_AL2)
            .architecture(Architecture.X86_64)
            .build()
    }
}
