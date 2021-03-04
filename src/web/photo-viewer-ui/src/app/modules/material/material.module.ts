import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';

import { MatExpansionModule } from '@angular/material/expansion';

import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';

import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';

import { MatSelectModule } from '@angular/material/select';

import { MatGridListModule } from '@angular/material/grid-list'

import { MatDialogModule } from '@angular/material/dialog';

import { MatSidenavModule } from '@angular/material/sidenav';
//import { MatDialog } from '@angular/material/dialog';

import { MatCheckboxModule } from '@angular/material/checkbox';

import { MatRadioModule } from '@angular/material/radio';

//import {MatSortModule} from '@angular/material/sort';

import { MatPaginatorModule } from '@angular/material/paginator'

import { MatSnackBarModule } from '@angular/material/snack-bar';

import { MatMenuModule } from '@angular/material/menu';

import { DragDropModule } from '@angular/cdk/drag-drop';

import { MatStepperModule } from "@angular/material/stepper";

import { MatBadgeModule } from "@angular/material/badge";

import { MatTooltipModule } from "@angular/material/tooltip";

import { MatTabsModule } from "@angular/material/tabs";

import { MatProgressBarModule } from '@angular/material/progress-bar';

import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { MatDividerModule } from '@angular/material/divider';

import { MatListModule } from '@angular/material/list';

import { MatSlideToggleModule } from '@angular/material/slide-toggle';

import { MatRippleModule } from '@angular/material/core';

let matarray = [
  CommonModule,
  DragDropModule,
  MatBadgeModule,
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatCheckboxModule,
  MatDialogModule,
  MatExpansionModule,
  MatGridListModule,
  MatInputModule,
  MatIconModule,
  MatMenuModule,
  MatPaginatorModule,
  MatSortModule,
  MatRadioModule,
  MatSidenavModule,
  MatSortModule,
  MatSelectModule,
  MatSnackBarModule,
  MatStepperModule,
  MatTableModule,
  MatToolbarModule,
  MatTooltipModule,
  MatTabsModule,
  MatProgressBarModule,
  MatProgressSpinnerModule,
  MatDividerModule,
  MatListModule,
  MatSidenavModule,
  MatSlideToggleModule,
  MatRippleModule
]

@NgModule({
  imports: matarray,
  exports: matarray
})

export class MaterialModule { }
export * from '@angular/material/dialog';
export * from '@angular/material/snack-bar';
export * from '@angular/material/table';
export { MatPaginator, PageEvent } from '@angular/material/paginator';
export { MatSort, MatSortable } from '@angular/material/sort';