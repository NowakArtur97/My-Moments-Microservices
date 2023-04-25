import { Routes } from '@angular/router';

import { AuthWrapperComponent } from './auth/auth-wrapper/auth-wrapper.component';
import { APP_ROUTES } from './common/const.data';
import { FollowersComponent } from './follower/followers/followers.component';
import { FollowingComponent } from './follower/following/following.component';
import MyFollowersResolver from './follower/resolver/my-followers.resolver';
import MyFollowingResolver from './follower/resolver/my-following.resolver';
import { PostWrapperComponent } from './post/post-wrapper/post-wrapper.component';
import { PostsComponent } from './post/posts/posts.component';
import MyPostsResolver from './post/resolver/my-posts.resolver';

const routes: Routes = [
  { path: '', redirectTo: APP_ROUTES.auth, pathMatch: 'full' },
  { path: APP_ROUTES.auth, component: AuthWrapperComponent },
  {
    path: APP_ROUTES.post.posts,
    component: PostsComponent,
    // canActivate: [AuthGuard],
    resolve: { posts: MyPostsResolver },
  },
  {
    path: APP_ROUTES.post.editor,
    component: PostWrapperComponent,
    // canActivate: [AuthGuard],
  },
  {
    path: APP_ROUTES.follower.followers,
    component: FollowersComponent,
    // canActivate: [AuthGuard],
    resolve: { posts: MyFollowersResolver },
  },
  {
    path: APP_ROUTES.follower.following,
    component: FollowingComponent,
    // canActivate: [AuthGuard],
    resolve: { posts: MyFollowingResolver },
  },
  { path: '**', component: AuthWrapperComponent },
];
export default routes;
