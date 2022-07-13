package com.example.testdb.firstScreen.firstSubScreen

import android.graphics.Color
import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testdb.databinding.ItemInfoBinding
import com.example.testdb.modelsDb.InfoDb
import com.example.testdb.utils.COST
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

interface  InfoActionListener {
    fun onInfoDetails(info: InfoDb)
}

class InfoDiffUtilsCallback(
    private val oldList: List<InfoDb>,
    private val newList: List<InfoDb>
): DiffUtil.Callback(){
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}

class InfoAdapter(private val actionListener: InfoActionListener):
    RecyclerView.Adapter<InfoAdapter.InfoViewHolder>(), View.OnClickListener {

    var infos: List<InfoDb> = emptyList()
        set(newValue) {
            val diffRez = DiffUtil.calculateDiff(InfoDiffUtilsCallback(field, newValue))
            field = newValue
            diffRez.dispatchUpdatesTo(this)
        }

    override fun onClick(v: View) {
        val info = v.tag as InfoDb
        actionListener.onInfoDetails(info)
    }

    override fun getItemCount(): Int = infos.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemInfoBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)
        return InfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val myInfo = infos[position]

        with(holder.binding) {
            holder.itemView.tag = myInfo
            val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale("ru"))
            val dateNF = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale("ru"))
                .parse(dateFormat.format(Date(myInfo.time)))
            val rusDate = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM, Locale("ru"))
                .format(dateNF!!)

            cat.text = myInfo.cat + ":"
            date.text = rusDate
            count.text = myInfo.number.toString() + " руб."
            name.text = myInfo.sName + " " + myInfo.fName + " " + myInfo.tName
            if (myInfo.type == COST){
                cv.setCardBackgroundColor(Color.parseColor("#C56765"))
                lr.gravity = Gravity.LEFT
            }
            else {
                cv.setCardBackgroundColor(Color.parseColor("#B9FBC6"))
                lr.gravity = Gravity.RIGHT
            }
        }
    }

    class InfoViewHolder(
        val binding: ItemInfoBinding
    ) : RecyclerView.ViewHolder(binding.root)
}