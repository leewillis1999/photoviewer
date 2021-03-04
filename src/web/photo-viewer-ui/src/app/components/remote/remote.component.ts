import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@app/modules/material/material.module';
import { RemoteService } from '@app/services/remote.service';

@Component({
  selector: 'app-remote',
  templateUrl: './remote.component.html',
  styleUrls: ['./remote.component.css']
})
export class RemoteComponent implements OnInit {

  //public processing: boolean = false;
  public playing: boolean = true;
  public loading: boolean = false;

  public currentImage: any = {};

  constructor(
    private remote: RemoteService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.getCurrentPicture();
  }

  likeArray(count: number): number[] {
    if (count) {
      return Array.from(Array(count).keys());
    }
    return [];
  }

  getCurrentPicture() {
    this.loading = true;
    this.remote.getCurrentPicture()
      .subscribe(
        (data) => {
          console.log(data);
          this.currentImage = data;
        },
        (er) => {
          console.error(er);
        }
      ).add(
        () => {
          this.loading = false;
        }
      )
  }

  onClickPrev() {
    this.loading = true;
    this.remote.previousPicture()
      .subscribe(
        data => {
          console.log(data);
          this.currentImage = data;
        }
      )
      .add(
        () => {
          this.loading = false
        }
      )
  }

  onClickNext() {
    this.loading = true;
    this.remote.nextPicture()
      .subscribe(
        data => {
          console.log(data);
          this.currentImage = data;
        }
      )
      .add(
        () => {
          this.loading = false
        }
      )
  }

  onClickPlay() {
    //this.playing = !this.playing;
  }

  onClickLike() {
    this.loading = true;
    this.remote.likePicture()
      .subscribe(
        data => {
          console.log(data);
          this.currentImage = data;
        }
      )
      .add(
        () => {
          this.loading = false;
          this.snackBar.open("Picture liked", "X", { duration: 2000 });
        }
      )
  }

  onClickUnlike() {
    this.loading = true;
    this.remote.unlikePicture()
      .subscribe(
        data => {
          console.log(data);
          this.currentImage = data;
        }
      )
      .add(
        () => {
          this.loading = false;
          this.snackBar.open("Picture unliked", "Close", { duration: 2000 });
        }
      )
  }

  onClickDelete() {
    this.loading = true;
    this.remote.deletePicture()
      .subscribe(
        data => {
          console.log(data)
        }
      )
      .add(
        () => {
          this.loading = false;
          this.snackBar.open("Picture deleted", "Close", { duration: 2000 });
          this.getCurrentPicture();
        }
      )
  }
}
