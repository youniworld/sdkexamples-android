package com.easemob.applib.widget.chatrow;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.ContextMenu;
import com.easemob.chatuidemo.activity.ShowBigImage;
import com.easemob.chatuidemo.task.LoadImageTask;
import com.easemob.chatuidemo.utils.ImageCache;
import com.easemob.chatuidemo.utils.ImageUtils;
import com.easemob.util.EMLog;

public class EMChatRowImage extends EMChatRowFile{

    protected ImageView imageView;

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
        bubbleLayout.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((Activity)context).startActivityForResult(
                        (new Intent(context, ContextMenu.class)).putExtra("position", position).putExtra("type",
                                EMMessage.Type.IMAGE.ordinal()), REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });
        // 接收方向的消息
        if (message.direct == EMMessage.Direct.RECEIVE) {
            if (message.status == EMMessage.Status.INPROGRESS) {
                imageView.setImageResource(R.drawable.default_image);
                showDownloadPregress((ImageMessageBody)message.getBody());
            } else {
                progressBar.setVisibility(View.GONE);
                percentageView.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.default_image);
                ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
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
        
        handleSendMessage((ImageMessageBody)message.getBody());
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
            bubbleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EMLog.d(TAG, "image view on click");
                    Intent intent = new Intent(context, ShowBigImage.class);
                    File file = new File(localFullSizePath);
                    if (file.exists()) {
                        Uri uri = Uri.fromFile(file);
                        intent.putExtra("uri", uri);
                    } else {
                        // The local full size pic does not exist yet.
                        // ShowBigImage needs to download it from the server
                        // first
                        ImageMessageBody body = (ImageMessageBody) message
                                .getBody();
                        intent.putExtra("secret", body.getSecret());
                        intent.putExtra("remotepath", remote);
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
            });
            return true;
        } else {

            new LoadImageTask().execute(thumbernailPath, localFullSizePath,
                    remote, message.getChatType(), iv, context, message);
            return true;
        }
    }

}
