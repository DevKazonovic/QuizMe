package com.my.projects.quizapp.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.my.projects.quizapp.data.model.Category
import com.my.projects.quizapp.databinding.CardCategoryBinding
import com.my.projects.quizapp.databinding.CardQuestionBinding

class CategoriesAdapter(private val list:List<Category>, val listener:OnItemClickListener): RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>(){

    interface OnItemClickListener{
        fun onItemClick(cat: Category)
    }

    class CategoriesViewHolder(private val itemBinding:CardCategoryBinding)
        :RecyclerView.ViewHolder(itemBinding.root){

        fun bind(cat:Category, listener: OnItemClickListener){
            itemBinding.txtCatName.text=cat.name
            itemBinding.root.setOnClickListener {
                listener.onItemClick(cat)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = CardCategoryBinding.inflate(inflater, parent, false)
        return CategoriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        holder.bind(list[position],listener)
    }

    override fun getItemCount(): Int = list.size
}