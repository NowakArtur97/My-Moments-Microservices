import { HttpClient, HttpErrorResponse } from '@angular/common/http';

import ErrorResponse from '../models/error-response.model';

export default abstract class HttpService {
  constructor(protected httpClient: HttpClient) {}

  protected defaultErrorResponse: ErrorResponse = {
    status: 500,
    timestamp: new Date(),
    errors: ['Something went wrong.', 'Please try again in a moment.'],
  };

  protected createFormData(
    dataToAppend: { key: string; value: any }[]
  ): FormData {
    const multipartData = new FormData();
    dataToAppend.forEach(({ key, value }) =>
      multipartData.append(key, JSON.stringify(value))
    );
    return multipartData;
  }

  protected createFormDataFromFiles(
    filesToAppend: { key: string; files: File[] }[],
    dataToAppend: { key: string; value: any }[] = []
  ): FormData {
    const multipartData = new FormData();
    filesToAppend.forEach(({ key, files }) =>
      files.forEach((file) => multipartData.append(key, file, file.name))
    );
    dataToAppend.forEach(({ key, value }) => {
      multipartData.append(key, JSON.stringify(value));
    });
    return multipartData;
  }

  protected logErrors(httpErrorResponse: HttpErrorResponse): void {
    if (this.isErrorResponse(httpErrorResponse)) {
      console.log(true);
      console.log(httpErrorResponse as HttpErrorResponse);
    } else {
      console.log(httpErrorResponse);
    }
  }

  protected isErrorResponse = (httpErrorResponse: HttpErrorResponse): boolean =>
    (httpErrorResponse.error as ErrorResponse)?.errors !== undefined;
}
