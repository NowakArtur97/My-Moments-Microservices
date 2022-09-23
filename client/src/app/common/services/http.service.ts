import { HttpClient, HttpErrorResponse } from '@angular/common/http';

import ErrorResponse from '../models/error-response.model';

export default abstract class HttpService {
  constructor(protected httpClient: HttpClient) {}

  protected defaultErrorResponse: ErrorResponse = {
    status: 500,
    timestamp: new Date(),
    errors: ['Something went wrong.', 'Please try again in a moment.'],
  };

  protected createFormdata(
    dataToAppend: { key: string; value: any }[]
  ): FormData {
    const multipartData = new FormData();
    dataToAppend.forEach(({ key, value }) =>
      multipartData.append(key, JSON.stringify(value))
    );
    return multipartData;
  }

  protected isErrorResponse = (httpErrorResponse: HttpErrorResponse): boolean =>
    (httpErrorResponse.error as ErrorResponse).errors !== undefined;
}
