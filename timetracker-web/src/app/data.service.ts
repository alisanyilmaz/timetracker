import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  readonly HTTP_OPTIONS = {
    headers: new HttpHeaders({
      'Content-Type':  'application/json'
    })
  };

  readonly PATH_RECORDS = '/api/records';

  constructor(private http: HttpClient) { }

  private extractData(res: Response) {
    let body = res;
    return body || { };
  }

  getTimeTrackRecords(emailAddr:string, offset, length): Observable<any> {
    return this.http.get(this.PATH_RECORDS + '?email=' + emailAddr + '&offset=' + offset + '&length=' + length).pipe(
      map(this.extractData),
      catchError(this.handleError<any>('getTimeTrackRecords'))
    );
  }

  postTimeTrackRecord(timeTrackRecord: any) {
    return this.http.post<any>(this.PATH_RECORDS, JSON.stringify(timeTrackRecord), this.HTTP_OPTIONS).pipe(
      tap((record) => console.log('added a timetrack record w/ id=${record.email}')),
      catchError(this.handleError<any>('postTimeTrackRecord'))
    );
  }

  private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      console.error(error);
      console.log(`${operation} failed: ${error.message}`);

      if(error.status == 400) { // Bad request
        console.log("Parameters are invalid!");
        window.alert("Parameters are invalid!");
      }else if(error.status == 404) { // Not found
        console.log("Can not reach to the server!");
        window.alert("Can not reach to the server!");
      }else if(error.status == 500) { // Internal server error
        console.log("Internal server error!");
        window.alert("Internal server error!");
      }

      return of(result as T);
    };
  }

}
