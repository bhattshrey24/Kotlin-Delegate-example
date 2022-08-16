package com.example.kotlinsdelegateexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.security.acl.Owner
import kotlin.reflect.KProperty


//delegation is an object oriented design pattern
// see the concept explanation in intellij first
class MainActivity : AppCompatActivity(),
    AnalyticsLogger by AnalyticsLoggerImp(), // see we are giving interface and its implementation , its like inheritance
    DeepLinkHandler by DeepLinkHandlerImp() // and we can do this more than once
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerLifeCycleOwner(this) // we passed this activity as the 'lifeCycleOwner'
        // to this function and see we are accessing these function as if they
         // were functions of our parent class
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        handleDeepLink(intent)

    }
}

interface AnalyticsLogger { // suppose we want to log when user comes
    // to the app and when user leaves it
    fun registerLifeCycleOwner(owner: LifecycleOwner) // we will need lifecycleowner
// for this ie. the activity/fragment
}

class AnalyticsLoggerImp() : AnalyticsLogger,
    LifecycleEventObserver { // we need to implement 'LifecycleEventObserver' inorder
    // to make it log users activity

    override fun registerLifeCycleOwner(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this) // ie. this class will act as an observer
        // ie. when user leaves or enters this app this class will get that event and since
        // this class implements 'LifecycleEventObserver' therefore 'onStateChanged'
        // function will be called
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                println("User opened the screen")
            }
            Lifecycle.Event.ON_PAUSE -> {
                println("User leaves the screen")
            }
            else -> Unit // ie. we don't wanna do anything in rest of the lifecycle events
        }
    }
}

interface DeepLinkHandler {
    fun handleDeepLink(intent: Intent?)
}

class DeepLinkHandlerImp() : DeepLinkHandler {
    override fun handleDeepLink(intent: Intent?) {
        println("Handling deep link!!!!!!")
    }
}


// below are the example of property delegate
val ob by lazy {  // this is provided by android
    println("Hey!!")
    24
}
val myOb by MyLazy { // This is my lazyInitializer
    println("Hello from my lazy")
    28
}

// This is similar to how actual 'lazy' is implemented , not same but close.
class MyLazy<out T : Any>( // the 'out' keyword is used for generics in kotlin

    private val initialize: () -> T // since it only takes one parameter
    // and that parameter is a function therefore while
    // using it we can use it without using '()' ie. instead of this 'val ob by lazy(){ }' we use 'val ob by lazy{ }'
) {
    private var value: T? = null

    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): T { // this is operator overloading , we are overloading
        // the getter provided by kotlin , ignore the parameters that we
        // passed we don't need them it's just so that we don't get error

        return if (value == null) { // we check if value is null or not , if its null we initialize it
            value = initialize()
            value!! // last statement of 'if' is returned therefore this will be returned
        } else value!! // if 'value' is not null then no need to initialize it , simply return it
    }

}