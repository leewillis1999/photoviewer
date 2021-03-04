import { Component } from '@angular/core';
import { RemoteService } from './services/remote.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'photo-viewer-ui';

  constructor(
    private remoteService: RemoteService
  ) {

  }

  onClickButton() {
    console.log("Button clicked");
    this.remoteService.sendMessage("Testing...")
      .subscribe(
        (data) => {
          console.log(data);
        },
        (er) => {
          console.error(er);
        }
      )
  }
}
