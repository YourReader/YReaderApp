package read.code.yourreader.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import read.code.yourreader.data.Files
import read.code.yourreader.databinding.ListitemBinding
import java.io.File

@SuppressLint("SetTextI18n")
class FilesAdapter : ListAdapter<Files, FilesAdapter.FilesViewHolder>(DiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        val binding = ListitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class FilesViewHolder(private val binding: ListitemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(files: Files) {
            val file = File(files.path)
            binding.apply {
                fileNameList.text = file.name.replace(files.type, "")
                fileTypeListItem.text = files.type
                sizeFileListItem.text = "${
                    "%.2f".format(file.length().toFloat() / 1048576.0)
                } MB"
            }
        }
    }

    class DiffCallBack : DiffUtil.ItemCallback<Files>() {
        override fun areItemsTheSame(oldItem: Files, newItem: Files) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Files, newItem: Files) =
            oldItem == newItem

    }
}