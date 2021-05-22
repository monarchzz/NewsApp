package vn.edu.trunghieu.newsapp.model

import vn.edu.trunghieu.newsapp.R

sealed class ItemObjectBottomSheet(val image: Int, val name: String){
    class Save(
        image: Int = R.drawable.ic_baseline_bookmark_border_24,
        name: String = "Save to read"
    ) : ItemObjectBottomSheet(image, name)

    class Delete(
        image: Int = R.drawable.ic_baseline_bookmark_24,
        name: String = "Delete from saved news"
    ) : ItemObjectBottomSheet(image, name)

    class Share(
        image: Int = R.drawable.ic_baseline_share_24,
        name: String = "Share"
    ) : ItemObjectBottomSheet(image, name)

    class GoToNewsPage(
        image: Int = R.drawable.ic_baseline_open_in_new_24,
        name: String
    ) : ItemObjectBottomSheet(image, "Go to $name")

}