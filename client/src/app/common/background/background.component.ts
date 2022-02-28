import { Component, HostListener, OnInit } from '@angular/core';

import { BackgroundService } from './background.service';

@Component({
  selector: 'app-background',
  templateUrl: './background.component.html',
  styleUrls: ['./background.component.css'],
})
export class BackgroundComponent implements OnInit {
  tiles!: String[][];
  tileHeight!: String;
  gridColumns!: String;

  @HostListener('window:resize', ['$event'])
  private onResize(): void {
    this.setupTiles();
  }

  constructor(private backgroundService: BackgroundService) {
    this.setupTiles();
  }

  ngOnInit(): void {}

  setupTiles() {
    this.tiles = this.backgroundService.getRandomImages();
    this.tileHeight = this.backgroundService.getTileHeight();
    this.gridColumns = `repeat(${this.tiles.length}, 1fr)`;
  }
}
