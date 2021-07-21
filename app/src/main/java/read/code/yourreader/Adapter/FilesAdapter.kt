package read.code.yourreader.Adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import read.code.yourreader.data.Files
import read.code.yourreader.databinding.ListitemBinding
import java.io.File
import java.util.*

@SuppressLint("SetTextI18n")
class FilesAdapter(private val listener: OnCardViewClickListener) :
    ListAdapter<Files, FilesAdapter.FilesViewHolder>(DiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        val binding = ListitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        val currentItem = getItem(position)
        Log.d("HELLO", "onBindViewHolder: currentItem ${currentItem.type}")
        holder.bind(currentItem)
    }

    inner class FilesViewHolder(private val binding: ListitemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private lateinit var bitmap: Bitmap

        init {
            binding.cardViewItem.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION)
                listener.onCardClick(adapterPosition)
        }

        @SuppressLint("DefaultLocale")
        fun bind(files: Files) {
            val file = File(files.path)
            binding.apply {
                fileNameList.text = file.name.replace(files.type, "")
                fileTypeListItem.text = files.type.replace(".", "").uppercase(Locale.getDefault())
                sizeFileListItem.text = "${
                    "%.2f".format(file.length().toFloat() / 1048576.0)
                } MB"
                Log.d("HELLO", "bind:  name ${file.name}")

                if (files.type == ".pdf") {
                    val fd =
                        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                    val renderer = PdfRenderer(fd)
                    bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_4444)
                    val page: PdfRenderer.Page = renderer.openPage(0)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                    imgListItem.setImageBitmap(bitmap)
                }
            }
        }
    }

    interface OnCardViewClickListener {
        fun onCardClick(position: Int)
    }

    class DiffCallBack : DiffUtil.ItemCallback<Files>() {
        override fun areItemsTheSame(oldItem: Files, newItem: Files) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Files, newItem: Files) =
            oldItem == newItem

    }
}