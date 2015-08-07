package com.easemob.chatuilib.widget.chatrow;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chatuilib.R;
import com.easemob.chatuilib.utils.ImageCache;
import com.easemob.chatuilib.utils.ImageUtils;
import com.easemob.util.EMLog;

public class EMChatRowImage extends EMChatRowFile{

    protected ImageView imageView;
    private ImageMessageBody imgBody;

    public EMChatRowImage(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct == EMMessage.Direct.RECEIVE ? R.layout.em_row_received_picture : R.layout.em_row_sent_picture, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(R.id.percentage);
        imageView = (ImageView) findViewById(R.id.image);
    }

    
    @Override
    protected void onSetUpView() {
        imgBody = (ImageMessageBody) message.getBody();
        // 接收方向的消息
        if (message.direct == EMMessage.Direct.RECEIVE) {
            if (message.status == EMMessage.Status.INPROGRESS) {
                imageView.setImageResource(R.drawable.em_default_image);
                showDownloadPregress((ImageMessageBody)message.getBody());
            } else {
                progressBar.setVisibility(View.GONE);
                percentageView.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.em_default_image);
                if (imgBody.getLocalUrl() != null) {
                    // String filePath = imgBody.getLocalUrl();
                    String remotePath = imgBody.getRemoteUrl();
                    String filePath = ImageUtils.getImagePath(remotePath);
                    String thumbRemoteUrl = imgBody.getThumbnailUrl();
                    String thumbnailPath = ImageUtils.getThumbnailImagePath(thumbRemoteUrl);
                    showImageView(thumbnailPath, imageView, filePath, imgBody.getRemoteUrl(), message);
                }
            }
            return;
        }
        
        String filePath = imgBody.getLocalUrl();
        if (filePath != null) {
            showImageView(ImageUtils.getThumbnailImagePath(filePath), imageView, filePath, null, message);
        } 
        handleSendMessage((ImageMessageBody)message.getBody());
    }
    
    @Override
    protected void onUpdateView() {
        super.onUpdateView();
    }
    
    @Override
    protected void onBuubleClick() {
        Intent intent = new Intent(context, ShowBigImage.class);
        File file = new File(imgBody.getLocalUrl());
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            intent.putExtra("uri", uri);
        } else {
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
            intent.putExtra("secret", imgBody.getSecret());
            intent.putExtra("remotepath", imgBody.getRemoteUrl());
        }
        if (message != null
                && message.direct == EMMessage.Direct.RECEIVE
                && !message.isAcked
                && message.getChatType() != ChatType.GroupChat) {
            try {
                EMChatManager.getInstance().ackMessageRead(
                        message.getFrom(), message.getMsgId());
                message.isAcked = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        context.startActivity(intent);
    }
    
    /**
     * load image into image view
     * 
     * @param thumbernailPath
     * @param iv
     * @param position
     * @return the image exists or not
     */
    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath, String remoteDir,
            final EMMessage message) {
        final String remote = remoteDir;
        EMLog.d(TAG, "local = " + localFullSizePath + " remote: " + remote);
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
            return true;
        } else {

            new LoadImageTask().execute(thumbernailPath, localFullSizePath,
                    remote, message.getChatType(), iv, context, message);
            return true;
        }
    }

}
