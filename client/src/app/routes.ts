import { Routes } from '@angular/router';

import { AuthWrapperComponent } from './auth/auth-wrapper/auth-wrapper.component';
import { APP_ROUTES } from './common/const.data';
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
    path: APP_ROUTES.post.new,
    component: PostWrapperComponent,
    // canActivate: [AuthGuard],
  },
  { path: '**', component: AuthWrapperComponent },
];
export default routes;
