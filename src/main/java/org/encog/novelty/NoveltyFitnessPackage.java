/*
 * Encog(tm) Core v3.4 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2017 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.novelty;

import java.io.Serializable;

public class NoveltyFitnessPackage implements Serializable {

	private double fitness;
	private NoveltyData data;

	public NoveltyFitnessPackage(double fitness, NoveltyData data) {
		this.fitness = fitness;
		this.data = data;
	}

	public double getFitness() {
		return fitness;
	}

	public NoveltyData getData() {
		return data;
	}
}
