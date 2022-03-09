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
      transition('down => up', animate('{{randomTiming}}ms'), {
        params: { randomTiming: '0' },
      }),
      transition('up => down', animate('{{randomTiming}}ms'), {
        params: { randomTiming: '0' },
      }),
    ]),
  ],
})
export class BackgroundComponent implements OnInit, AfterViewInit {
  private readonly TILES_ANIMATIONS_STATES = { UP: 'up', DOWN: 'down' };
  private readonly ANIMATIONS_TIMINGS = { MIN: 1000, MAX: 3000 };

  state = this.TILES_ANIMATIONS_STATES.UP;
  tiles!: String[][];
  tileHeight!: String;
  gridColumns!: String;
  tilesTopOffset!: String;
  randomTiming!: number;

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
    this.gridColumns = `repeat(${this.tiles.length}, 1fr)`;
    this.randomTiming = this.generateRandomTiming();
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.state = this.TILES_ANIMATIONS_STATES.DOWN;
    }, 0);
  }

  onEnd(event: AnimationEvent) {
    this.state = this.TILES_ANIMATIONS_STATES.UP;
    this.randomTiming = this.generateRandomTiming();
    if (event.toState === this.TILES_ANIMATIONS_STATES.UP) {
      setTimeout(() => {
        this.state = this.TILES_ANIMATIONS_STATES.DOWN;
      }, 0);
    }
  }

  private generateRandomTiming = (): number =>
    Math.floor(
      Math.random() *
        (this.ANIMATIONS_TIMINGS.MAX - this.ANIMATIONS_TIMINGS.MIN + 1) +
        this.ANIMATIONS_TIMINGS.MIN
    );
}
