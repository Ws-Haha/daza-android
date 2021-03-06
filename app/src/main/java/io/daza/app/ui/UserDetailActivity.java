/**
 * Copyright (C) 2015 JianyingLi <lijy91@foxmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.daza.app.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.blankapp.annotation.ViewById;
import org.blankapp.util.ViewUtils;

import io.daza.app.R;
import io.daza.app.handler.ErrorHandler;
import io.daza.app.model.Model;
import io.daza.app.model.Result;
import io.daza.app.model.User;
import io.daza.app.ui.base.BaseActivity;
import io.daza.app.util.Auth;
import io.daza.app.util.Thumbnail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.daza.app.api.ApiClient.API;

public class UserDetailActivity extends BaseActivity {

    private int mUserId = 0;
    private User mUser = null;

    @ViewById(R.id.btn_profile)
    private LinearLayout mBtnProfile;
    @ViewById(R.id.iv_avatar)
    private ImageView mIvAvatar;
    @ViewById(R.id.tv_name)
    private TextView mTvName;
    @ViewById(R.id.tv_bio)
    private TextView mTvBio;
    @ViewById(R.id.btn_own_topics)
    private Button mBtnOwnTopics;
    @ViewById(R.id.btn_own_subscribes)
    private Button mBtnOwnSubscribes;
    @ViewById(R.id.btn_own_upvotes)
    private Button mBtnOwnUpvotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        mUserId = getIntent().getIntExtra("extra_user_id", 0);
        mUser = Model.parseObject(getIntent().getStringExtra("extra_user"), User.class);

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Uri data = getIntent().getData();
            mUserId = Integer.parseInt(data.getPathSegments().get(0));
        }

        mBtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        mBtnOwnTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Auth.check()) {
                    Intent intent = new Intent(UserDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(UserDetailActivity.this, OwnTopicsActivity.class);
                    intent.putExtra("extra_user", mUser.toJSONString());
                    intent.putExtra("extra_user_id", mUserId);
                    startActivity(intent);
                }
            }
        });

        mBtnOwnSubscribes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Auth.check()) {
                    Intent intent = new Intent(UserDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(UserDetailActivity.this, OwnSubscribesActivity.class);
                    intent.putExtra("extra_user", mUser.toJSONString());
                    intent.putExtra("extra_user_id", mUserId);
                    startActivity(intent);
                }
            }
        });

        mBtnOwnUpvotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Auth.check()) {
                    Intent intent = new Intent(UserDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(UserDetailActivity.this, OwnUpvoteArticlesActivity.class);
                    intent.putExtra("extra_user", mUser.toJSONString());
                    intent.putExtra("extra_user_id", mUserId);
                    startActivity(intent);
                }
            }
        });

        if (Auth.check() && mUserId == Auth.id()) {
            mBtnOwnTopics.setText("我的主题");
            mBtnOwnSubscribes.setText("我订阅的");
            mBtnOwnUpvotes.setText("我赞过的");
        }

        initView();

        if (mUser == null) {
            API.getUser(mUserId).enqueue(new Callback<Result<User>>() {
                @Override
                public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                    if (response.isSuccessful()) {
                        mUser = response.body().getData();
                        initView();
                    }
                }

                @Override
                public void onFailure(Call<Result<User>> call, Throwable t) {
                    new ErrorHandler(UserDetailActivity.this).handleError(t);
                }
            });
        }
    }

    private void initView() {
        if (mUser == null) {
            return;
        }
        Glide
                .with(this)
                .load(new Thumbnail(mUser.getAvatar_url()).small())
                .centerCrop()
                .placeholder(R.mipmap.placeholder_image)
                .crossFade()
                .into(mIvAvatar);
        mTvName.setText(mUser.getName());
        mTvBio.setText(mUser.getBio());
        ViewUtils.setGone(mTvBio, false);
    }

}
