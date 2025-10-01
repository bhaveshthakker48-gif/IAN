package org.bombayneurosciences.bna_2023.Activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bombayneurosciences.bna_2023.Model.CommiteeDataClass
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.adapter.Commetteadapter
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import org.bombayneurosciences.bna_2023.utils.SessionManager1
import org.bombayneurosciences.bna_2023.utils.SessionManagerSingleton
import org.bombayneurosciences.bna_2023.utils.sharepreferenceAppkill
import pl.droidsonroids.gif.GifImageView

class CommetteeActivity : AppCompatActivity() {

    var CommiteeArrayList:ArrayList<CommiteeDataClass>?=null
    private lateinit var sharedPreferencesManager1: SessionManager1
    private lateinit var SharepreferenceAppkill : sharepreferenceAppkill
    var isLogin=false
    var isKeepLogged=false
lateinit var SessionManager:SessionManager1
    private lateinit var gifImageView: GifImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commettee)


        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = true
        window.statusBarColor = Color.WHITE

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply padding to the activity content (this handles all root layouts properly)
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }


        val layoutPrivacy: RelativeLayout = findViewById(R.id.layout_privacy)
        val backbutton: ImageView = findViewById(R.id.backbutton)
        val commeteText: TextView = findViewById(R.id.commeteText)
       gifImageView = findViewById(R.id.gifImageView)

// Post a delayed action to make layout_privacy visible after 5 seconds
//        Handler(Looper.getMainLooper()).postDelayed({
//            backbutton.visibility = View.VISIBLE
//            commeteText.visibility = View.VISIBLE        }, 100) // 5000 milliseconds (5 seconds)

        val backButton = findViewById<ImageView>(R.id.backbutton)
        sharedPreferencesManager1 = SessionManager1(this)
        SharepreferenceAppkill= sharepreferenceAppkill(this)
        SessionManager = SessionManagerSingleton.getSessionManager(this)

        // Retrieve values from the intent
         isLogin = intent.getBooleanExtra("Login", false)
         isKeepLogged = intent.getBooleanExtra("Keep_logged", false)

        // Now you can use these values in your CommetteeActivity
        Log.d(ConstanstsApp.tag, "Is Login: $isLogin, Keep Logged: $isKeepLogged")



        // Set an OnClickListener for the back button
        backButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            //    sharedPreferencesManager1.setBackState(true)
//                sharedPreferencesManager1.getUserStatus()
//                SessionManager.setLogin(isLogin,isKeepLogged)
//
//                Log.d(ConstanstsApp.tag, "getLogin in commitee=>"+sharedPreferencesManager1.getUserStatus())
//

            }
        })

        CommiteeArrayList = ArrayList()


        CommiteeArrayList!!.add(
           /* CommiteeDataClass(
                R.drawable.batuk,
                "Dr. Batuk",
                "Diyora",
                "President"
            )*/
                    CommiteeDataClass(
                    R.drawable.muzumdar,
            "Dr. Dattatraya",
            "Muzumdar",
            "President"
        )
        )

        CommiteeArrayList!!.add(
           /* CommiteeDataClass(
                R.drawable.muzumdar,
                "Dr. Dattatraya",
                "Muzumdar",
                "President Elect"
            )*/
            CommiteeDataClass(
                R.drawable.rajeshbenny,
                "Dr. Rajesh",
                "Benny",
                "President Elect"
            )
        )
        CommiteeArrayList!!.add(
           /* CommiteeDataClass(
                R.drawable.rajeshbenny,
                "Dr. Rajesh",
                "Benny",
                "Secretary"
            )*/
                    CommiteeDataClass(
                    R.drawable.sudheer_ambekar,
            "Dr. Sudheer",
            "Ambekar",
            "Secretary"
        )
        )
        CommiteeArrayList!!.add(
          /*  CommiteeDataClass(
                R.drawable.trimurtinadkarni,
                "Dr. Trimurti",
                "Nadkarni",
                "Treasurer"
            )*/
            CommiteeDataClass(
                R.drawable.r_k_s,
                "Dr. Rakesh",
                "Singh",
                "Treasurer"
            )
        )
        CommiteeArrayList!!.add(
           /* CommiteeDataClass(
                R.drawable.sudheerambekar,
                "Dr. Sudhir",
                "Ambekar",
                "Executive Member"
            )*/
            CommiteeDataClass(
                R.drawable.gurneet_singh_sawhney,
                "Dr. Gurneet Singh",
                "Sawhney",
                "Executive Member"
            )
        )
        CommiteeArrayList!!.add(
           /* CommiteeDataClass(
                R.drawable.rakesh1,
                "Dr. Rakesh",
                "Singh",
                "Executive Member"
            )*/
            CommiteeDataClass(
                R.drawable.ashutosh_shetty,
                "Dr. Ashutosh",
                "Shetty",
                "Executive Member"
            )
        )
        CommiteeArrayList!!.add(
          /*  CommiteeDataClass(
                R.drawable.joseph,
                "Dr. Joseph",
                "Monteiro",
                "Executive Member"
            )*/
                    CommiteeDataClass(
                R.drawable.shwetal_goraksha,
                "Dr. Shwetal",
                "Goraksha",
                "Executive Member"
            )
        )
       /* CommiteeArrayList!!.add(
            CommiteeDataClass(
                R.drawable.charu2,
                "Dr. Charulata",
                "Sankhla",
                "Past President"
            )
        )*/
       /* CommiteeArrayList!!.add(
            CommiteeDataClass(
                R.drawable.ketan,
                "Dr. Ketan",
                "Desai",
                "Past President"
            )
        )*/
        CommiteeArrayList!!.add(
            CommiteeDataClass(
                R.drawable.ganesh,
                "Dr. Ganesh",
                "Kini",
                "Past President"
            )
        )
        CommiteeArrayList!!.add(
            CommiteeDataClass(
                R.drawable.batuk,
                "Dr. Batuk",
                "Diyora",
                "Past President"
            )
        )

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)


        recyclerview.layoutManager = LinearLayoutManager(this)


        val adapter = Commetteadapter(this, CommiteeArrayList!!)
        recyclerview.adapter = adapter
    }
        override fun onBackPressed() {
            super.onBackPressed()
            val(isLogin,isKeep)=SessionManager.getUserStatus()

          //  sharedPreferencesManager1.setBackState(isKeep)
          // sharedPreferencesManager1.setBackState(true)

//            sharedPreferencesManager1.setLogin(isLogin,isKeepLogged)
//            val sessionManager = SessionManagerSingleton.getSessionManager(this)
//
//            sharedPreferencesManager1.getUserStatus()
//            Log.d(ConstanstsApp.tag, "getLogin in commitee=>"+sharedPreferencesManager1.getUserStatus())
//
//           val (isLogin,isKeep)= sessionManager.getUserStatus()
//


            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

    }

//    override fun onStop() {
//        super.onStop()
//        SharepreferenceAppkill.clearAppKill()
//        Log.d(ConstanstsApp.tag, "getAppKill=>"+SharepreferenceAppkill.getAppKill())
//
//    }
}