import { Component } from '@angular/core';
import { ClickAndDragToScrollService } from 'src/app/common/services/click-and-drag-to-scroll.service';

import { PostsComponent } from '../posts/posts.component';
import { PostService } from '../services/post.service';

@Component({
  selector: 'app-following-posts',
  templateUrl: '../posts/posts.component.html',
  styleUrls: ['../posts/posts.component.css'],
})
export class FollowingPostsComponent extends PostsComponent {
  constructor(
    protected postService: PostService,
    clickAndDragToScrollService: ClickAndDragToScrollService
  ) {
    super(postService, clickAndDragToScrollService);
    this.postsSubscription = this.postService.followingPosts;
  }
}
