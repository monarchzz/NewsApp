package vn.edu.trunghieu.newsapp.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import vn.edu.trunghieu.newsapp.R
import vn.edu.trunghieu.newsapp.databinding.FragmentBottomSheetBinding
import vn.edu.trunghieu.newsapp.databinding.ItemBottomSheetBinding
import vn.edu.trunghieu.newsapp.model.ItemObjectBottomSheet

class BottomSheetFragment(
    private val dataList: List<ItemObjectBottomSheet>
    ) : BottomSheetDialogFragment(){

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var onItemClickListener: ((ItemObjectBottomSheet) -> Unit)? = null
    fun setOnClickListener(listener: ((ItemObjectBottomSheet) -> Unit)) {
        onItemClickListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentBottomSheetBinding.inflate(LayoutInflater.from(context))

        val bottomSheetDialog : BottomSheetDialog =  super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        val view = binding.root.apply {
            dataList.forEach { itemObject ->
                val itemBinding = ItemBottomSheetBinding.inflate(LayoutInflater.from(context)).apply {
                    ivIconItem.setImageResource(itemObject.image)
                    tvTextItem.text = itemObject.name
                    (root as View).setOnClickListener {
                        onItemClickListener?.let {
                            it(itemObject)
                        }
                    }
                }

                addView(itemBinding.root)
            }

        } as View
        bottomSheetDialog.setContentView(view)

        return bottomSheetDialog
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}