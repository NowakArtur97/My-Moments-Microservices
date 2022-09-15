import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.local';

import ImageSnippet from '../models/image-snippet.model';

@Injectable({ providedIn: 'root' })
export class PostService {
  constructor(private httpClient: HttpClient) {}

  createPost(files: ImageSnippet[]) {
    console.log('send');
    const multipartData = new FormData();
    multipartData.append('photos', JSON.stringify(files));
    this.httpClient
      .post<any>(`${environment.postServiceUrl}`, multipartData)
      .subscribe(
        (res: any) => {
          console.log(res);
          return null;
        },
        (res: any) => console.log(res)
      );
  }
}
