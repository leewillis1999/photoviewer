import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class RemoteService {

  private REMOTE_URL: string = "/api/remote"
  constructor(
    private http: HttpClient
  ) { }

  sendMessage(msg: string): Observable<any> {
    var url = `${this.REMOTE_URL}/remote`;
    console.log("Sending message " + msg + " to " + url);
    return this.http.post<string>(url, msg);
  }

  getCurrentPicture(): Observable<any> {
    var url = "/api/screen/current";
    return this.http.get(url);
  }

  nextPicture(): Observable<any> {
    var url = `${this.REMOTE_URL}/nextPicture`;
    console.log(url);
    return this.http.get(url);
  }

  previousPicture(): Observable<any> {
    var url = `${this.REMOTE_URL}/prevPicture`;
    return this.http.get(url);
  }

  likePicture(): Observable<any> {
    var url = `${this.REMOTE_URL}/like`;
    return this.http.get(url);
  }

  unlikePicture(): Observable<any> {
    var url = `${this.REMOTE_URL}/dontlike`;
    return this.http.get(url);
  }

  deletePicture(): Observable<any> {
    var url = `${this.REMOTE_URL}/delete`;
    return this.http.get(url);
  }

}
