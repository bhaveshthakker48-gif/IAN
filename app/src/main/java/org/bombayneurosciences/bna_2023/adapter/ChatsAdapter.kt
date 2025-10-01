package org.bombayneurosciences.bna_2023.adapter

import android.content.Intent
import android.os.AsyncTask
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import org.bombayneurosciences.bna_2023.Activity.RepliesActivity
import org.bombayneurosciences.bna_2023.Model.chats.ChatsResponse
import org.bombayneurosciences.bna_2023.Model.chats.Data
import org.bombayneurosciences.bna_2023.R
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale

class ChatsAdapter(private var chatsList: List<Data>,private val delegateId: String,private val isArchived: Boolean) :
    RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {






    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commFromTextView: TextView = itemView.findViewById(R.id.commFromTextView)
        val commtype :TextView = itemView.findViewById(R.id.commenttype)
//        val replybutton:RelativeLayout = itemView.findViewById(R.id.replybutton)
//        val replyimg:ImageView = itemView.findViewById(R.id.repliimg)
//        val replytext:TextView = itemView.findViewById(R.id.replytext)
        val replyImageView: ImageView = itemView.findViewById(R.id.repliimg)
        val replyTextView: TextView = itemView.findViewById(R.id.replytext)
        val replyImageView1: ImageView = itemView.findViewById(R.id.repliimg1)
        val replyTextView1: TextView = itemView.findViewById(R.id.replytext1)
        val replyCountTextView: TextView = itemView.findViewById(R.id.replyCountTextView)
        val replyCountTextView1: TextView = itemView.findViewById(R.id.replyCountTextView1)

        val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
        val createdAtTextView: TextView = itemView.findViewById(R.id.createdAtTextView)
        val commFromTextView1: ImageView = itemView.findViewById(R.id.commFromTextView1)
        val commentTextView1: TextView = itemView.findViewById(R.id.commentTextView1)
        val createdAtTextView1: TextView = itemView.findViewById(R.id.createdAtTextView1)
        val receive_cardView: CardView = itemView.findViewById(R.id.receive_cardView)
        val send_cardView: CardView = itemView.findViewById(R.id.send_cardView)
        init {
            receive_cardView.setOnClickListener {
                val context = it.context
                val intent = Intent(context, RepliesActivity::class.java)
                // Pass the caseId to RepliesActivity
                intent.putExtra("case_id", chatsList[adapterPosition].case_id.toString())
                intent.putExtra("comment", chatsList[adapterPosition].comment)
                  intent.putExtra("id",chatsList[adapterPosition].id)
                intent.putExtra("isArchived", isArchived)
                context.startActivity(intent)
            }
        }
        init {
            send_cardView.setOnClickListener {
                val context = it.context
                val intent = Intent(context, RepliesActivity::class.java)
                // Pass the caseId to RepliesActivity
                intent.putExtra("case_id", chatsList[adapterPosition].case_id.toString())
                intent.putExtra("comment", chatsList[adapterPosition].comment)
                intent.putExtra("id",chatsList[adapterPosition].id)
                intent.putExtra("isArchived", isArchived)
                context.startActivity(intent)
            }
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)


    }
    private fun countRepliesForComments(commentsList: List<Data>): Map<Int, Int> {
        val commentRepliesCountMap = mutableMapOf<Int, Int>()

        for (comment in commentsList) {
            if (comment.reTocommid != 0) {
                // This comment is a reply to another comment
                val parentId = comment.reTocommid
                commentRepliesCountMap[parentId] = commentRepliesCountMap.getOrDefault(parentId, 0) + 1
            }
        }

        return commentRepliesCountMap
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatItem = chatsList[position]
        fetchRepliesFromApi(chatItem.case_id,holder,chatItem.id)

        Log.d(ConstanstsApp.tag,"chatItem.id=>"+chatItem.id)



// Parse the date from the server's format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val parsedDate = dateFormat.parse(chatItem.created_at)

       // countReplies(chatItem.id)


        // Format the date to your desired format
        val formattedDate = when {
            DateUtils.isToday(parsedDate.time) -> {
                "Today " + SimpleDateFormat("hh:mm ", Locale.getDefault()).format(parsedDate)
            }

            else -> {
                SimpleDateFormat("dd/MM/yyyy hh:mm ", Locale.getDefault()).format(parsedDate)
            }
        }
        if (chatItem.represent == 1) {
            // Show the reply indicator for chats where represent is 1
            holder.replyImageView.visibility = View.VISIBLE
            holder.replyTextView.visibility = View.VISIBLE
            holder.replyImageView1.visibility = View.VISIBLE
            holder.replyTextView1.visibility = View.VISIBLE
        } else {
            // Hide the reply indicator for other chats
            holder.replyImageView.visibility = View.GONE
            holder.replyTextView.visibility = View.GONE
            holder.replyImageView1.visibility = View.GONE
            holder.replyTextView1.visibility = View.GONE
        }

        val commentRepliesCountMap = countRepliesForComments(chatsList)

        // Now, you can use commentRepliesCountMap to get the count of replies for each comment
        val commentId = chatsList[position].id
        val replyCount = commentRepliesCountMap[commentId] ?: 0

//        // Set visibility based on the reply count
//        if (replyCount > 0) {
//            holder.replyCountTextView.visibility = View.VISIBLE
//            holder.replyCountTextView1.visibility = View.VISIBLE
//
//            holder.replyCountTextView.text = "Replies: $replyCount"
//        } else {
//            holder.replyCountTextView.visibility = View.GONE
//            holder.replyCountTextView1.visibility = View.GONE
//
//        }

    //  holder.commFromTextView.text = chatItem.commFrom
       // holder.commFromTextView.text=chatItem.commFromname
        holder.commentTextView.text = chatItem.comment.toString()
        holder.createdAtTextView.text = formattedDate

        holder.commentTextView1.text = chatItem.comment.toString()
        holder.createdAtTextView1.text = formattedDate


        when (chatItem.commFrom) {
            "Member" -> {
                // For Member, set the commFromTextView text and hide commtype
                holder.commFromTextView.text = chatItem.commFromname
                holder.commtype.visibility = View.GONE
            }

            "Moderator" -> {
                // For Moderator, set the commFromTextView text and show commtype
                holder.commFromTextView.text = chatItem.commFromname
                holder.commtype.text = "(${chatItem.commFrom})"
                holder.commtype.visibility = View.VISIBLE
            }

            "Admin" -> {
                // For Admin, set the commFromTextView text and show commtype
                holder.commFromTextView.text = " "
                holder.commtype.text = chatItem.commFrom
                holder.commtype.visibility = View.VISIBLE
            }
        }



        if (delegateId.toInt() == chatItem.commFromid) {

            holder.send_cardView.visibility = View.VISIBLE
            holder.receive_cardView.visibility = View.GONE

            Log.d(ConstanstsApp.tag, "equal")
        } else {
            holder.send_cardView.visibility = View.GONE
            holder.receive_cardView.visibility = View.VISIBLE
            Log.d(ConstanstsApp.tag, "not equal")
        }
     //   updateReplyButtonVisibility(holder, chatItem.represent)

    }

   /* fun countReplies(chatID: Int): Int {
        val url = "https://www.telemedocket.com/BNA/public/getrecomments?case_id=$chatID"
        try {
            val jsonString = URL(url).readText()
            val response = Gson().fromJson(jsonString, ChatsResponse::class.java)
            return response.data.size // Assuming the response contains a list of replies
        } catch (e: Exception) {
            Log.e(ConstanstsApp.tag, "Error counting replies: ${e.message}", e)
            return 0
        }
    }*/


        fun fetchRepliesFromApi(id: Int, holder: ViewHolder, comment_id1: Int) {

            Log.d(ConstanstsApp.tag,"fetchRepliesFromApi"+id)
        FetchRepliesTask(id,holder,comment_id1).execute()
    }
    // Inside your ChatsAdapter, you can add a function to make the API call
    inner class FetchRepliesTask(id: Int, holder: ViewHolder, comment_id1: Int) : AsyncTask<Void, Void, List<Data>>() {



        var id=id
        var comment_id1=comment_id1

        var holder=holder



        override fun doInBackground(vararg params: Void?): List<Data>? {

            Log.d(ConstanstsApp.tag,"id in adapter=>"+id)
            return try {
                // Replace this URL with your actual API endpoint
                val url = "https://www.telemedocket.com/BNA/public/getrecomments?case_id="+id
                Log.d(ConstanstsApp.tag,"url in adapter=>"+url)
                val jsonString = URL(url).readText()

                // Parse the JSON response using Gson
                //val response = Gson().fromJson(jsonString, ChatsResponse::class.java)

               /* val response: ChatsResponse = Gson().fromJson(jsonString, ChatsResponse::class.java)*/
               // val commentCount: Int = response.data.flatMap { it.comment }.size


                val response: ChatsResponse = Gson().fromJson(jsonString, ChatsResponse::class.java)


                val repliedCommentsIds: List<Int> = response.data
                    .filter { it.is_replied == "1" }
                    .map { it.id }

                Log.d(ConstanstsApp.tag, "Replied Comments Ids: $repliedCommentsIds")
                Log.d(ConstanstsApp.tag, "Replied Comments Ids: $comment_id1")

                val repliedCommentsPairs: List<Pair<Int, Int>> = response.data
                    .filter { it.is_replied == "1" }
                    .map { comment_id1 to it.reTocommid }

                Log.d(ConstanstsApp.tag, "Replied Comments Pairs: $repliedCommentsPairs")

                val countSameCommentId1AndReTocommid: Int = repliedCommentsPairs
                    .count { it.first == it.second }

                Log.d(ConstanstsApp.tag, "Count of pairs with the same comment_id1 and reTocommid: $countSameCommentId1AndReTocommid")


                if (countSameCommentId1AndReTocommid==0)
                {
                    holder.replyCountTextView.visibility=View.GONE
                    holder.replyCountTextView1.visibility=View.GONE

                }
                else
                {
                    holder.replyCountTextView.visibility=View.VISIBLE
                    holder.replyCountTextView1.visibility=View.VISIBLE
                    holder.replyCountTextView.text=countSameCommentId1AndReTocommid.toString()
                    holder.replyCountTextView1.text=countSameCommentId1AndReTocommid.toString()

                }

                // Return the list of replies
                response.data
            } catch (e: Exception) {
                Log.e(ConstanstsApp.tag, "Error fetching replies: ${e.message}", e)
                null
            }
        }

        override fun onPostExecute(result: List<Data>?) {
            if (result != null) {
                Log.d(ConstanstsApp.tag, "Failed to fetch replies"+result.size)

                for (i in 0 until result.size)
                {
                    val data=result[i]
                  //  val reply_count=data.is_replied
                    val represent = data.represent
                    val replyCount = data.replyCount
// Assuming you have a method in your adapter to update the visibility of the reply button
                   // updateReplyButtonVisibility(holder, represent)

//                    when(represent)
//                    {
//                        0->
//                        {
//                           holder.replybutton.visibility= View.GONE
//                        }
//                        else->
//                        {
//                            holder.replybutton.visibility= View.VISIBLE
//
//                         //   holder.replytext.text=reply_count+" "+"Replies"
//                            Log.d(ConstanstsApp.tag, "replies count=>"+reply_count)
//                        }
//                    }

                }

               // adapter.updateChatsFromApi(result)
            } else {
                Log.d(ConstanstsApp.tag, "Failed to fetch replies")
            }
        }
    }

    fun updateReplyCountVisibility(holder: ViewHolder, replyCount: Int) {
        Log.d(ConstanstsApp.tag, "Updating reply count and visibility: $replyCount")

        // Set visibility based on the reply count
        if (replyCount > 0) {
            holder.replyCountTextView.visibility = View.VISIBLE
            holder.replyCountTextView.text = "Replies: $replyCount"
        } else {
            holder.replyCountTextView.visibility = View.GONE
        }
    }


//    fun updateReplyButtonVisibility(holder: ViewHolder, represent: Int) {
//        Log.d(ConstanstsApp.tag, "Updating reply button visibility: $represent")
//
//        if (represent == 1) {
//            // If represent is 1, show the reply button and text
//            holder.replyImageView.visibility = View.VISIBLE
//            holder.replyTextView.visibility = View.VISIBLE
//            holder.replyImageView1.visibility = View.VISIBLE
//            holder.replyTextView1.visibility = View.VISIBLE
//        } else {
//            // If represent is 0, hide the reply button and text
//            holder.replyImageView1.visibility = View.GONE
//            holder.replyTextView1.visibility = View.GONE
//        }
//    }
//

    // Update your ChatsAdapter to handle the updated data
    fun updateChatsFromApi(responseData: List<Data>) {
        chatsList = responseData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return chatsList.size
    }

    fun updateChats(result: List<Data>) {
        chatsList = result
        notifyDataSetChanged()
    }

}