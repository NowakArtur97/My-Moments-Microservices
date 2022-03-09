import { animate, AnimationEvent, state, style, transition, trigger } from '@angular/animations';
import { AfterViewInit, Component, HostListener, OnInit } from '@angular/core';

import { BackgroundService } from './background.service';

@Component({
  selector: 'app-background',
  templateUrl: './background.component.html',
  styleUrls: ['./background.component.css'],
  animations: [
    trigger('move', [
      state(
        'down',
        style({
          transform: 'translateY(-{{tilesTopOffset }} )',
        }),
        { params: { tilesTopOffset: '0' } }
      ),
      state(
        'up',
        style({
          transform: 'translateY({{tilesTopOffset }} )',
        }),
        { params: { tilesTopOffset: '0' } }
      ),
      transition('down => up', animate('3000ms')),
      transition('up => down', animate('3000ms')),
    ]),
  ],
})
export class BackgroundComponent implements OnInit, AfterViewInit {
  private readonly TILES_ANIMATIONS_STATES = { UP: 'up', DOWN: 'down' };
  tilesTopOffset!: String;
  state = this.TILES_ANIMATIONS_STATES.UP;
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
    const tileHeight = this.backgroundService.getTileHeight();
    this.tileHeight = `${tileHeight}px`;
    this.tilesTopOffset = `-${
      tileHeight *
      (this.backgroundService.NUMBER_OF_BACKUP_IMAGES_FOR_ANIMATION / 2)
    }px`;
    console.log(this.tilesTopOffset);
    this.gridColumns = `repeat(${this.tiles.length}, 1fr)`;
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.state = this.TILES_ANIMATIONS_STATES.DOWN;
    }, 0);
  }

  onEnd(event: AnimationEvent) {
    this.state = this.TILES_ANIMATIONS_STATES.UP;
    if (event.toState === this.TILES_ANIMATIONS_STATES.UP) {
      setTimeout(() => {
        this.state = this.TILES_ANIMATIONS_STATES.DOWN;
      }, 0);
    }
  }
}
