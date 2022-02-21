package com.livelike.demo.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.livelike.demo.databinding.PinHybridMessageBinding

class PinnedTextMessageHolder(binding: PinHybridMessageBinding): RecyclerView.ViewHolder(binding.root)  {
    var bindingObject = binding
    var messageId: String = ""
}
